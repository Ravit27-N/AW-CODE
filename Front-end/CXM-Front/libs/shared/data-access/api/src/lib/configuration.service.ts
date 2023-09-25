import { Injectable } from "@angular/core";
import { appSettingsProviderFactory, IAppSettings } from "../../../../app-config/src";

@Injectable({
  providedIn: 'root'
})
export class ConfigurationService {

  getAppSettings(): IAppSettings {
    return appSettingsProviderFactory();
  }
}
