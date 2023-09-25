import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import * as moment from 'moment';
import { initialState } from './modification.reducers';
import {
  ClientFormModel,
  ClientModel,
  ConfigurationForm,
  ConfigurationsModel,
  CriteriaDistributionFormModel,
  DepositModes,
  Entries,
  IClientDepositModePayload,
  Preference,
  PreferencePayload,
  PublicHolidayModel,
} from '../../models';

export const DEFAULT_SECTION_KEY = 'DEFAULT';
const capitalize = (str: string) => {
  if (typeof str === 'string') {
    return str.replace(/^\w/, (c) => c.toUpperCase());
  } else {
    return '';
  }
};

const fromUtcTime = (utcTime: string) => {
  const t = moment.utc(utcTime, "HH:mm");
  return t.local().format("HH:mm");
}

const toUtcTime = (time: string) => {
  const t = moment(time, "HH:mm").utc();
  return t.format("HH:mm");
}

export const prepareCreateClientPayload = (clientData: any, divisionData: any, functionalities: any) => {
  const divisions = divisionData.map((division: any) => {
    return {
      id: 0,
      name: division.name,
      address: division.address || null,
      services: division.services
        .map((service: any) => ({
          id: 0,
          name: service.name,
          address: service.address || null,
        })),
    }
  });

  return {
    id: 0,
    name: clientData.name,
    email: clientData.email,
    contactFirstName: clientData.contactFirstName,
    contactLastname: clientData.contactLastname,
    fileId: clientData.file.fileId,
    filename: clientData.file.filename,
    fileSize: clientData.file.fileSize,
    address: clientData.address || null,
    divisions,
    functionalities
  };
}

export const prepareClientInfoResponse = (clientInfo: ClientModel): ClientFormModel => {

  const {
    name, contactFirstName, contactLastname, email,
    filename, fileSize, fileId,
    divisions, functionalities, unloads, publicHolidays,
    fillers, depositModes, portalConfigEnable, criteriaDistributions,
    address,
  } = clientInfo;

  return  {
    client: {
      name,
      contactFirstName,
      contactLastname,
      email,
      file: { filename, fileSize, fileId },
      address: address || null,
    },
    divisions,
    functionalities,
    unloads,
    publicHolidays,
    fillers,
    depositModes,
    criteriaDistributions,
    portalConfigEnable,
  }
}


export const prepareModifyClientPayload = (clientId: any, clientData: any, divisionData: any, functionalities: any,
                                           offloadConfig: any, fillers: any, depositModes: any, portalConfigEnable: boolean) => {
  const divisions = divisionData.map((division: any) => {
    return {
      id: division.id || 0,
      name: division.name,
      services: division.services
        .map((service: any) => ({
          id: service.id || 0,
          name: service.name,
          address: service.address || null,
        })),
      address: division.address || null,
    }
  });

  const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
  let { byDays, byHolidays } = offloadConfig;

  byHolidays = byHolidays.filter((x: any) => x.check).map((x: any) => x.id);

  byDays = byDays.flatMap((x: any) => {
    const unloadingDays = x.hours.map((h: any) => ({ id: x.id || 0, dayOfWeek: x.label, time: h, enabled: x.check, zoneId: timezone }));
    if (x.check && x.hours.length === 0) {
      unloadingDays.push({ id: x.id || 0, dayOfWeek: x.label, time: '', enabled: x.check, zoneId: timezone });
    }
    return unloadingDays;
  });

  const isAdmin = UserUtil.isAdmin();
  return {
    id: clientId,
    name: clientData.name,
    email: clientData.email,
    contactFirstName: clientData.contactFirstName,
    contactLastname: clientData.contactLastname,
    fileId: clientData.file.fileId,
    filename: clientData.file.filename,
    fileSize: clientData.file.fileSize,
    address: clientData.address,
    divisions,
    functionalities: isAdmin ? functionalities : [],
    publicHolidays: byHolidays,
    unloads: byDays,
    fillers,
    depositModes,
    portalConfigEnable,
  };
}


export const aggregateRemovedDivision = (oldDivision: any, newDivision: any) => {
  const divisions = Array.from(oldDivision).filter((x: any) => newDivision.includes((y: any) => y.id === x.id)).map((z: any) => z.id)

  return { divisions }
}


export const aggregateOffloadConfig = (offloadConfig: any, offload: any, publicHolidays: any, byHolidays: any) => {
  let { byDays } = offloadConfig;

  byHolidays = byHolidays.map((d: any) => publicHolidays.includes(d.id) ? { ...d, check: true } : d);

  byDays = [...byDays];
  offload.forEach((o: any, i: number) => {
    const index = Array.from(byDays).findIndex((d: any) => d.label === o.dayOfWeek);
    byDays[index] = { ...byDays[index], hours: [...byDays[index].hours, o.time]}

    let hours = byDays[index].hours;
    hours = Array.from(hours).sort((a: any, b: any) => a.localeCompare(b));
    byDays[index].hours = hours;

    byDays[index].check = o.enabled == true ? true : byDays[index].check;
  });

  return {
    byDays,
    byHolidays,
    ready: true

  }
}

const getHolidayTooltip = (eventDate: string, locale: string): string => {
  try {
    moment.locale(locale);
    return capitalize(moment(eventDate, 'MM/DD').format('dddd DD/MM/yyyy'))
  } catch (e) {
    console.warn(e);
    return ''
  }
};

const getHolidayTooltip2 = (eventDate: any, locale: string): string => {
  try {
    moment.locale(locale);
    return capitalize(moment(eventDate).format('dddd DD/MM/yyyy'));
  } catch (e) {
    console.warn(e);
    return ''
  }
};

export const aggregateHoliday = (holidays: Array<PublicHolidayModel>) => {
  const locale = localStorage.getItem('locale') || 'fr';

  return Array.from(holidays)
  .map((x: any) => {

    if(x.fixedDate === false) {
      const { publicHolidayDetails } = x;
      const y = new Date().getFullYear();

      const tooltip = publicHolidayDetails
      .filter((z: any) => z.year===y).map((d: any) => getHolidayTooltip2(d, locale)).join(', ');

      return { id: x.id, label: x.label, check: false, day: x.day, month: x.month, holidayTooltip: `[${tooltip}]`}
    }

    return { id: x.id, label: x.label, check: false, day: x.day, month: x.month, holidayTooltip: getHolidayTooltip(x.eventDate, locale)}
  })
}


export const aggregateFillers = (clientFilter: any, stateFillers: any) => {
  return Array.from(stateFillers).map((x: any)=> {
    const found = Array.from(clientFilter).find((y: any) => y.key === x.key) as any;
    return found ? { ...x, ...found } : x;
  })
}

export const checkFormHasChange = (allStates: any) => {

  const { offloadConfig,  fillers, clientId, mode, client, divisions, functionalities, depositModesPayload, portalConfigEnable, beforeClientModify  } = allStates;

  const payload = prepareCreateClientPayload(allStates.client, allStates.divisions, Array.from(allStates.functionalities).sort(sortFunctionality));
  const originalInfo = prepareCreateClientPayload(
    mode ? beforeClientModify?.clientInfo?.client : initialState.client,
    mode ? beforeClientModify?.clientInfo?.divisions : initialState.divisions,
    mode ? Array.from( beforeClientModify?.clientInfo?.functionalities).sort(sortFunctionality) : initialState.functionalities
  );

  if (!mode) {
    return JSON.stringify(payload) !== JSON.stringify(originalInfo);
  }

  const modifyPayload = prepareModifyClientPayload(clientId, client, divisions, functionalities, offloadConfig, fillers, depositModesPayload, portalConfigEnable);

  const o = {
    data: originalInfo,
    unloads: removeIdFromUnloading(beforeClientModify?.clientInfo?.unloads),
    publicHolidays: beforeClientModify?.clientInfo?.publicHolidays,
    fillers: removeIdFromFiler(beforeClientModify?.clientInfo?.fillers),
    depositModes: mapAndSortDepositModes(beforeClientModify?.clientInfo?.depositModes, functionalities),
  };

  const p = {
    data: payload,
    unloads: removeIdFromUnloading(modifyPayload.unloads),
    publicHolidays: modifyPayload.publicHolidays,
    fillers: removeIdFromFiler(modifyPayload.fillers),
    depositModes: mapAndSortDepositModes(modifyPayload.depositModes, functionalities),
  };

  return JSON.stringify(o) !== JSON.stringify(p);
}

const removeIdFromUnloading = (state: Array<any>): any[] => {
  return state.map(s => {
    const { dayOfWeek, time, enabled } = s;
    return { dayOfWeek, time, enabled };
  }).sort(sortAlgorithm);
};

const removeIdFromFiler = (state: Array<any>): any[] => {
  return state.map(s => {
    const { key, value, enabled } = s;
    return { key, value, enabled }
  }).sort(sortAlgorithm);
}

const mapAndSortDepositModes = (state: Array<any>, functionalities: Array<string>): Array<any> => {
  state = JSON.parse(JSON.stringify(state));

  state = Array.from(DepositModes as IClientDepositModePayload[])
    .map(dm => {
      if (dm.key === 'flow.traceability.deposit.mode.portal') {
        return { ...dm, scanActivation: functionalities.includes('cxm_flow_deposit') }
      }
      const scanActivation = state.find((d: any) => d.scanActivation && d.key == dm.key);
      return { ...dm, scanActivation: Boolean(scanActivation) };
    });
  return JSON.parse(JSON.stringify(state)).filter((s: any) => s.value !== 'API').sort(sortAlgorithm);
}

const sortAlgorithm = (a: any, b: any) => `${a.key}`.localeCompare(`${b.key}`);

const sortFunctionality = (a: any, b: any) => a.localeCompare(b);

export const configurationsToForms = (configurations: Array<ConfigurationsModel>, immutableConfigurations?: string[], isPreviewConfigurationMode?: boolean): ConfigurationForm[] => {
  return configurations.map((data) => {
    const { name, order } = data;
    let { entries } = data;
    const impute = immutableConfigurations?.some(
      (ic) => ic.trim() === name.trim()
    );

    entries = orderConfigurations(entries, name);
    const content = configurationToTextContent(entries, name);

    return {
      name,
      order,
      entries,
      draggable : impute ? !impute : !isPreviewConfigurationMode,
      modifiable: true,
      removable: !impute,
      content,
    };
  });
}

export const orderConfigurations = (entries: Entries[], sectionName: string): Entries[] => {
  const modelEntry = entries.find(entry => entry.key === 'Modele' && entry.value === sectionName);
  const firstMatchIndex = entries.findIndex(i => i.key === 'Modele' && i.value === sectionName);
  const modelEntries = entries.filter((kv, index) => index !== firstMatchIndex);
  return modelEntry? [modelEntry, ...modelEntries] : modelEntries;
}

export const contentToConfiguration = (configuration: ConfigurationForm, removeSection: boolean): { configurations: ConfigurationForm, hasSection: boolean } => {
  let entries = Array.from(configuration.content.split('\n')).filter(data => data?.trim()).map(data => {
    const key = data.split('=')[0].trim();
    const value = data.split('=').filter((v, index) => index !== 0).join().trim();
    return { key, value };
  });

  const name = entries.find(k => k.key === 'Modele')?.value ? entries.find(k => k.key === 'Modele')?.value : DEFAULT_SECTION_KEY;
  if (removeSection) {
    const firstMatchIndex = entries.findIndex(i => i.key === 'Modele' && i.value === name);
    entries = entries.filter((kv, index) => index !== firstMatchIndex);
  }

  entries = orderConfigurations(entries, name || '');
  const content = configurationToTextContent(entries, name || '');

  return { configurations: { ...configuration, content, entries, name: name || '' }, hasSection: name !== undefined};
}

export const configurationToTextContent = (entries: Entries[], sectionName: string) => {
  const Modele = (entries.find(k => k.key === 'Modele')?.value === sectionName) ? '' : `Modele=${sectionName}\n`;
  return entries.reduce((acc, curr) => {
    return acc.concat(`${curr.key}=${curr.value}\n`);
  }, sectionName === DEFAULT_SECTION_KEY ? '' : Modele);
}


export const createModifyConfigurationFilePayload = (configurations: Array<any>) => {
  return configurations.map((c: any) => ({ name: c.name, order: c.order, entries: c.entries }));
}

export const removeOrderProperty = (data: any) => {
  const { order, ...rest } = data;
  return rest;
}

export const immutableConfigurations = [
  "DEFAULT",
  "PORTAIL",
  "PORTAIL_ANALYSE",
  "PORTAIL_PREVIEW",
]

export const preferenceToDistributeCriteria = (criteriaDistributionForm: Array<CriteriaDistributionFormModel>, preferenceAPIResponse: Array<Preference>): Array<CriteriaDistributionFormModel> => {
  return  preferenceAPIResponse.reduce((prev, curr) => {
    prev = JSON.parse(JSON.stringify(prev));
    const indexChannel = prev.findIndex(prevElement => prevElement.name.toLowerCase() === curr.name.toLowerCase());
    prev[indexChannel].active = curr.active;
    prev[indexChannel].enabled = curr.enabled;
    prev[indexChannel].manageable = curr.name === "Digital" && curr.active;
    prev[indexChannel].categories = prev[indexChannel].categories.map((category) => {
      const infoCategory = curr.preferences.find(preference => preference.name.toLowerCase() === category.name.toLowerCase());
      return infoCategory? { ...category, active: infoCategory.active, enabled: infoCategory.enabled } : category;
      }
    );

    return prev;
  }, criteriaDistributionForm);
}

export const updateDistributeCriteria = (criteria: Array<CriteriaDistributionFormModel>, preference: PreferencePayload): Array<CriteriaDistributionFormModel> => {
  criteria = JSON.parse(JSON.stringify(criteria));
  const indexChannel = criteria.findIndex(prevElement => prevElement.name.toLowerCase() === preference.name.toLowerCase());
  if (indexChannel !== -1) {
    criteria[indexChannel].active = preference.active;
    criteria[indexChannel].manageable = preference.name === "Digital" && preference.active;
  } else {
    criteria = criteria.map(criteria => {
      const indexCategory = criteria.categories.findIndex(category => category.name.toLowerCase() === preference.name.toLowerCase());
      if (indexCategory !== -1) {
        criteria.categories[indexCategory].active = preference.active;
      }
      return criteria;
    });
  }
  return criteria;
}
