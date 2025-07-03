import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GrupoModel, GrupoUpdateModel } from './models/grupo.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GrupoService {
  private apiUrl = `${environment.appApiUrl}/presgrupo`;
  constructor(private http: HttpClient) { }


  getAll(params?: {
    page?: number;
    size?: number;
    sort?: string;
    direction?: string;
    totalRecords?: number;
    searchCriteria?: { [key: string]: string }
  }): Observable<any> {
    const queryParams: any = {};
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    queryParams.sort = params?.sort || 'fechaCreacion';
    queryParams.direction = params?.direction || 'desc';

    if (params?.totalRecords !== undefined) {
      queryParams.totalRecords = params.totalRecords.toString();
    }

    if (params?.searchCriteria) {
      Object.entries(params.searchCriteria).forEach(([key, value]) => {
        queryParams[key] = value;
      });
    }

    return this.http.get<any>(this.apiUrl, { params: queryParams });
  }

  //metodo para obtener un grupo por código de naturaleza paginado
  getByNaturaleza(codigoNaturaleza: string, params?: {
    page?: number;
    size?: number;
    sort?: string;
    direction?: string;
    totalRecords?: number;
    searchCriteria?: { [key: string]: string }
  }): Observable<any> {
    const queryParams: any = {};
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    queryParams.sort = params?.sort || 'fechaCreacion';
    queryParams.direction = params?.direction || 'desc';

    if (params?.totalRecords !== undefined) {
      queryParams.totalRecords = params.totalRecords.toString();
    }

    if (params?.searchCriteria) {
      Object.entries(params.searchCriteria).forEach(([key, value]) => {
        queryParams[key] = value;
      });
    }

    return this.http.get<any>(`${this.apiUrl}/naturaleza/${codigoNaturaleza}`, { params: queryParams });
  }


  getById(codigo: string): Observable<GrupoModel> {
    return this.http.get<GrupoModel>(`${this.apiUrl}/${codigo}`);
  }

  create(pn: GrupoModel): Observable<GrupoModel> {
    return this.http.post<GrupoModel>(`${this.apiUrl}/add`, pn);
  }

  update(codigo: string, pn: GrupoUpdateModel): Observable<GrupoModel> {
    return this.http.put<GrupoModel>(`${this.apiUrl}/update/${codigo}`, pn);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
