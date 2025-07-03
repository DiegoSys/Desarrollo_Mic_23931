import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface TipoCampo {
  id?:number;
  codigo: string;
  nombre: string;
  descripcion?: string;
  estado?: string;
  fechaCreacion?: Date;
  fechaModificacion?: Date;
  usuarioCreacion?: string;
  usuarioModificacion?: string;
}

@Injectable({
  providedIn: 'root'
})
export class TipoCampoService {
  private apiUrl = `${environment.appApiUrl}/tipo-campo`;

  constructor(private http: HttpClient) {}

  // Obtener todos los tipos de campo
  getAll(): Observable<TipoCampo[]> {
    return this.http.get<TipoCampo[]>(`${this.apiUrl}/list`);
  }

  // Obtener todos paginado
  getAllPaginated(params?: {
    page?: number;
    size?: number;
    sort?: string;
    direction?: string;
    searchCriteria?: { [key: string]: string }
  }): Observable<any> {
    const queryParams: any = {};
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    queryParams.sort = params?.sort || 'fechaCreacion';
    queryParams.direction = params?.direction || 'desc';

    if (params?.searchCriteria) {
      Object.entries(params.searchCriteria).forEach(([key, value]) => {
        queryParams[key] = value;
      });
    }

    return this.http.get<any>(`${this.apiUrl}`, { params: queryParams });
  }

  // Obtener por código
  getByCodigo(codigo: string): Observable<TipoCampo> {
    return this.http.get<TipoCampo>(`${this.apiUrl}/${codigo}`);
  }

  // Obtener todos los tipos de campo activos
  getAllActivos(): Observable<TipoCampo[]> {
    return this.http.get<TipoCampo[]>(`${this.apiUrl}/activos`);
  }

  // Obtener activos paginado con filtros
  getAllActivosPaginated(params?: {
    page?: number;
    size?: number;
    sort?: string;
    direction?: string;
    searchCriteria?: { [key: string]: string }
  }): Observable<any> {
    const queryParams: any = {};
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    queryParams.sort = params?.sort || 'nombre';
    queryParams.direction = params?.direction || 'asc';
    
    // Agregar criterios de búsqueda
    if (params?.searchCriteria) {
      Object.entries(params.searchCriteria).forEach(([key, value]) => {
        queryParams[key] = value;
      });
    }

    return this.http.get<any>(`${this.apiUrl}/activos/paginado`, { params: queryParams });
  }

  // Crear nuevo tipo de campo
  create(tipoCampo: TipoCampo): Observable<TipoCampo> {
    return this.http.post<TipoCampo>(`${this.apiUrl}/add`, tipoCampo);
  }

  // Actualizar tipo de campo
  update(codigo: string, tipoCampo: TipoCampo): Observable<TipoCampo> {
    return this.http.put<TipoCampo>(`${this.apiUrl}/update/${codigo}`, tipoCampo);
  }

  // Eliminar tipo de campo
  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}