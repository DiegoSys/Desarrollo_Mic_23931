import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SubGrupoModel, SubGrupoUpdateModel } from './models/subGrupo.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SubGrupoService {
private apiUrl = `${environment.appApiUrl}/pressubgrupo`;
  constructor(private http: HttpClient) {}

  getAll(params?: { 
    page?: number;
    size?: number;
    sort?: string;
    order?: string;
    totalRecords?: number
  }): Observable<any> {
    const queryParams: any = {};
    
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    
    if (params?.sort) {
      queryParams.sort = params.sort;
      queryParams.order = params.order || 'asc';
    }

    if (params?.totalRecords !== undefined) {
      queryParams.totalRecords = params.totalRecords.toString();
    }

    return this.http.get<any>(this.apiUrl, { params: queryParams });
  }

  getById(codigo: string): Observable<SubGrupoModel> {
    return this.http.get<SubGrupoModel>(`${this.apiUrl}/${codigo}`);
  }

  create(pn: SubGrupoModel): Observable<SubGrupoModel> {
    return this.http.post<SubGrupoModel>(`${this.apiUrl}/add`, pn);
  }

  update(codigo: string, pn: SubGrupoUpdateModel): Observable<SubGrupoModel> {
    return this.http.put<SubGrupoModel>(`${this.apiUrl}/update/${codigo}`, pn);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
