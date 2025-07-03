import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SubProgramaModel, SubProgramaUpdateModel } from './models/subPrograma.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SubProgramaService {
  private apiUrl = `${environment.appApiUrl}/subprograma`;
  constructor(private http: HttpClient) {}

  getAll(params?: { 
    page?: number;
    size?: number;
    sort?: string;
    direction?: string;
    totalRecords?: number
  }): Observable<any> {
    const queryParams: any = {};
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    queryParams.sort = params?.sort || 'fechaCreacion';
    queryParams.direction = params?.direction || 'desc';
    if (params?.totalRecords !== undefined) {
      queryParams.totalRecords = params.totalRecords.toString();
    }
    return this.http.get<any>(this.apiUrl, { params: queryParams });
  }

  getAllActivos(): Observable<SubProgramaModel[]> {
    return this.http.get<SubProgramaModel[]>(`${this.apiUrl}/activos`);
  }

  getById(id: number): Observable<SubProgramaModel> {
    if (!id) {
      throw new Error('ID is required to fetch SubPrograma');
    }
    return this.http.get<SubProgramaModel>(`${this.apiUrl}/${id}`);
  }

  getByCodigoAndProgramaId(codigo: string, programaId: number): Observable<SubProgramaModel> {
    return this.http.get<SubProgramaModel>(`${this.apiUrl}/codigo/${codigo}/programa/${programaId}`);
  }

  getByProgramaIdAndEstado(programaId: number, params?: { 
    page?: number;
    size?: number;
    sort?: string;
    direction?: string;
    searchCriteria?: { [key: string]: string } // Nuevo campo opcional
  }): Observable<any> {
    const queryParams: any = {};
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    queryParams.sort = params?.sort || 'fechaCreacion';
    queryParams.direction = params?.direction || 'desc';

// Agregar criterios de búsqueda adicionales como query params
    if (params?.searchCriteria) {
      Object.entries(params.searchCriteria).forEach(([key, value]) => {
        queryParams[key] = value;
      });
    }

    return this.http.get<any>(`${this.apiUrl}/programa/${programaId}/estado`, { params: queryParams });
  }

  create(pn: SubProgramaModel): Observable<SubProgramaModel> {
    return this.http.post<SubProgramaModel>(`${this.apiUrl}/add`, pn);
  }

  createDefault(programaId: number): Observable<SubProgramaModel> {
    return this.http.post<SubProgramaModel>(`${this.apiUrl}/default/${programaId}`, {});
  }

  update(id: number, pn: SubProgramaUpdateModel): Observable<SubProgramaModel> {
    return this.http.put<SubProgramaModel>(`${this.apiUrl}/update/${id}`, pn);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}