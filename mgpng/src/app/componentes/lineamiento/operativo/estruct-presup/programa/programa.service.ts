import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ProgramaModel, ProgramaUpdateModel } from './models/programa.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProgramaService {
  private apiUrl = `${environment.appApiUrl}/programa`;

  constructor(private http: HttpClient) {}

  getAll(params?: { 
    page?: number;
    size?: number;
    sort?: string;
    direction?: string;
    totalRecords?: number;
    searchCriteria?: { [key: string]: string } // Nuevo campo opcional
  }): Observable<any> {
    const queryParams: any = {};
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    queryParams.sort = params?.sort || 'fechaCreacion';
    queryParams.direction = params?.direction || 'desc';

    if (params?.totalRecords !== undefined) {
      queryParams.totalRecords = params.totalRecords.toString();
    }

    // Agregar criterios de búsqueda adicionales como query params
    if (params?.searchCriteria) {
      Object.entries(params.searchCriteria).forEach(([key, value]) => {
        queryParams[key] = value;
      });
    }

    return this.http.get<any>(this.apiUrl, { params: queryParams });
  }

  getById(id: number): Observable<ProgramaModel> {
    return this.http.get<ProgramaModel>(`${this.apiUrl}/${id}`);
  }

  create(pn: ProgramaModel): Observable<ProgramaModel> {
    return this.http.post<ProgramaModel>(`${this.apiUrl}/add`, pn);
  }
  
  update(id: number, pn: ProgramaUpdateModel): Observable<ProgramaModel> {
    return this.http.put<ProgramaModel>(`${this.apiUrl}/update/${id}`, pn);
  }
  
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}