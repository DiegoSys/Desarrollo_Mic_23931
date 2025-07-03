import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ProyectoModel, ProyectoUpdateModel } from './models/proyecto.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProyectoService {
  private apiUrl = `${environment.appApiUrl}/proyecto`;

  constructor(private http: HttpClient) { }

  getById(id: number): Observable<ProyectoModel> {
    return this.http.get<ProyectoModel>(`${this.apiUrl}/${id}`);
  }

  getByProgramaAndSubprogramaPaginated(
    programaId: number,
    subProgramaId: number,
    params?: { 
      page?: number; 
      size?: number; 
      sort?: string; 
      direction?: string; 
      totalRecords?: number; 
      searchCriteria?: { [key: string]: string } // Nuevo campo opcional
    }
  ): Observable<any> {
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
    return this.http.get<any>(
      `${this.apiUrl}/programa/${programaId}/subprograma/${subProgramaId}/activos`,
      { params: queryParams }
    );
  }

  create(
    proyecto: ProyectoModel,
    programaId: number,
    subProgramaId: number
  ): Observable<ProyectoModel> {
    return this.http.post<ProyectoModel>(
      `${this.apiUrl}/add/programa/${programaId}/subprograma/${subProgramaId}`,
      proyecto
    );
  }

  createDefault(
    programaId: number,
    subProgramaId: number
  ): Observable<ProyectoModel> {
    return this.http.post<ProyectoModel>(
      `${this.apiUrl}/default/programa/${programaId}/subprograma/${subProgramaId}`,
      {}
    );
  }

  update(
    id: number,
    proyecto: ProyectoUpdateModel,
  ): Observable<ProyectoModel> {
    return this.http.put<ProyectoModel>(
      `${this.apiUrl}/update/${id}`,
      proyecto
    );
  }

  delete(
    id: number,
    programaId: number,
    subProgramaId: number
  ): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}/programa/${programaId}/subprograma/${subProgramaId}`
    );
  }
}