import {Injectable} from '@angular/core';
import {ApiService} from './api.service';
import {Observable} from 'rxjs';
import {ListProjectModel, ProjectModel} from '../model/ProjectModel';
import {HttpParams} from '@angular/common/http';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn:'root'
})

export class ProjectService {
  constructor(private apiService: ApiService) {}

  // eslint-disable-next-line max-len
  getList(index?: number,size?: number,filter?: string,isDeleted?: boolean,sortDirection?: string,sortByField?: string): Observable<ListProjectModel>{
    let params = new HttpParams();
    if (index > 0) { params = params.set('page', index.toString()); }
    if (size > 0) { params = params.set('pageSize', size.toString()); }
    if (filter) { params = params.set('filter', filter); }
    if (isDeleted !== undefined) { params = params.set('isDeleted', String(isDeleted)); }
    if (sortByField) {
      params = params.set('sortByField', sortByField);
    }
    if (sortDirection) {
      params = params.set('sortDirection', sortDirection);
    }
    return this.apiService.get(`${environment.rmsContextPath}/demand/project`,params);
  }

  getById(projectId: number|string): Observable<ProjectModel>{
    return this.apiService.get(`${environment.rmsContextPath}/demand/project/${projectId}`);
  }

  create(formData: ProjectModel): Observable<any>{
    return this.apiService.post(`${environment.rmsContextPath}/demand/project`,formData);
  }

  nameValidation(name: string): Observable<number> {
    return this.apiService.get(`${environment.rmsContextPath}/demand/project/validateName/${name}`);
  }

  validationUpdateName(id: number| string, name: string): Observable<number> {
    return this.apiService.get(`${environment.rmsContextPath}/demand/project/${id}/validateUpdateName/${name}`);
  }

  softDelete(id): Observable<any> {
    return this.apiService.patch(`${environment.rmsContextPath}/demand/project/${id}/delete/true`);
  }

  update(id: number|string,formData: ProjectModel): Observable<any>{
    return this.apiService.put(`${environment.rmsContextPath}/demand/project`,formData);
  }

  restore(id?: number | string, isDelete?: boolean): Observable<any> {
    return this.apiService.patch(`${environment.rmsContextPath}/demand/project/${id}/delete/${isDelete}`);
  }

  hardDelete(id?: number): Observable<any>{
    return this.apiService.delete(`${environment.rmsContextPath}/demand/project/${id}`);
  }

}
