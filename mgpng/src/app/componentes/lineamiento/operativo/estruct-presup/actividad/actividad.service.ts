import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ActividadModel, ActividadUpdateModel } from './models/actividad.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ActividadService {
  private apiUrl = `${environment.appApiUrl}/actividad`;

  constructor(private http: HttpClient) {}

  getById(id: number): Observable<ActividadModel> {
    return this.http.get<ActividadModel>(
      `${this.apiUrl}/${id}`
    );
  }

  getByProyectoPaginated(
    proyectoId: number,
    params?: { 
      page?: number;
      size?: number; 
      sort?: string; 
      direction?: string; 
      totalRecords?: number 
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
      `${this.apiUrl}/proyecto/${proyectoId}`,
      { params: queryParams }
    );
  }

  getAllActivosPaginated(
    params?: {
      page?: number;
      size?: number;
      sort?: string;
      direction?: string;
      searchCriteria?: { [key: string]: string }
    }
  ): Observable<any> {
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
  
    return this.http.get<any>(
      `${this.apiUrl}/list/paginated`,
      { params: queryParams }
    );
  }

  create(
    actividad: ActividadModel,
    proyectoId: number
  ): Observable<ActividadModel> {
    return this.http.post<ActividadModel>(
      `${this.apiUrl}/add/proyecto/${proyectoId}`,
      actividad
    );
  }

  createDefault(
    proyectoId: number
  ): Observable<ActividadModel> {
    return this.http.post<ActividadModel>(
      `${this.apiUrl}/default/proyecto/${proyectoId}`,
      {}
    );
  }

  update(
    id: number,
    proyectoId: number,
    actividad: ActividadUpdateModel
  ): Observable<ActividadModel> {
    return this.http.put<ActividadModel>(
      `${this.apiUrl}/update/${id}/proyecto/${proyectoId}`,
      actividad
    );
  }

  delete(
    id: number,
  ): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}/proyecto`
    );
  }
}