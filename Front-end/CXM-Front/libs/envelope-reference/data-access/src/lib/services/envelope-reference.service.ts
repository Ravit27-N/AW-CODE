import {CxmSettingService} from "@cxm-smartflow/shared/data-access/api";
import {Injectable} from "@angular/core";
import {
  CreateEnvelopeReference, PageEnvelopeReference,
  ResponseEnvelopeReference,
  SearchPageEnvelopeReference,
  UpdateEnvelopeReference
} from "../models";
import {Observable} from "rxjs";
import {settingEnv} from "@env-cxm-setting";
import {HttpParams} from "@angular/common/http";


@Injectable()
export class EnvelopeReferenceService {
  constructor(private readonly cxmSettingService: CxmSettingService) {
  }

  createEnvelopeReference(payload: CreateEnvelopeReference):Observable<ResponseEnvelopeReference>{
    return this.cxmSettingService.post(`${settingEnv.settingContext}/envelope-references`,payload);
  }

  updateEnvelopeReference(payload: UpdateEnvelopeReference):Observable<ResponseEnvelopeReference>{
    return this.cxmSettingService.put(`${settingEnv.settingContext}/envelope-references`,payload);
  }

  updateEnvelopeReferences(payload: UpdateEnvelopeReference, ids: string[]):Observable<ResponseEnvelopeReference>{
    return this.cxmSettingService.put(`${settingEnv.settingContext}/envelope-references/many?ids=${ids}`,payload);
  }

  searchEnvelopeReference(payload: SearchPageEnvelopeReference):Observable<PageEnvelopeReference>{
    const httpParams = new HttpParams()
      .set('keyword', payload.keyword )
      .set('page', payload.page )
      .set('size', payload.size )
      .set('sort', payload.sort.join(',') );
    return this.cxmSettingService.get(`${settingEnv.settingContext}/envelope-references`,httpParams);
  }
  findEnvelopeReferenceById(id: number):Observable<ResponseEnvelopeReference>{
    return this.cxmSettingService.get(`${settingEnv.settingContext}/envelope-references/${id}`);
  }
  deleteEnvelopeReferenceById(id: number):Observable<void>{
    return this.cxmSettingService.delete(`${settingEnv.settingContext}/envelope-references/${id}`);
  }
  deleteEnvelopeReferences(ids: string[]):Observable<void>{
    return this.cxmSettingService.delete(`${settingEnv.settingContext}/envelope-references/many?ids=${ids}`);
  }
  checkDuplicate(reference: string):Observable<boolean>{
    const httpParams = new HttpParams()
      .set('reference',reference );
    return this.cxmSettingService.get(`${settingEnv.settingContext}/envelope-references/check-duplicate`,httpParams);
  }
}
