import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ItemModel, ItemUpdateModel } from './models/item.model';
import { environment } from 'src/environments/environment';

/**
 * Servicio para gestionar operaciones CRUD y consultas paginadas de Items presupuestarios.
 */
@Injectable({
  providedIn: 'root'
})
export class ItemService {
  /** URL base del API para items presupuestarios */
  private apiUrl = `${environment.appApiUrl}/presitem`;

  constructor(private http: HttpClient) { }

  /**
   * Obtiene todos los items paginados y filtrados.
   * @param params Parámetros de paginación, orden y búsqueda.
   */
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

  /**
   * Obtiene items por código de subgrupo, paginados y filtrados.
   * @param codigoSubGrupo Código del subgrupo.
   * @param params Parámetros de paginación, orden y búsqueda.
   */
  getBySubGrupo(codigoSubGrupo: string, params?: {
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

    return this.http.get<any>(`${this.apiUrl}/subgrupo/${codigoSubGrupo}`, { params: queryParams });
  }

  /**
   * Obtiene todos los items activos paginados y filtrados usando el endpoint /list/paginated.
   * @param params Parámetros de paginación, orden y búsqueda.
   */
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

    return this.http.get<any>(`${this.apiUrl}/list/paginated`, { params: queryParams });
  }

  /**
   * Obtiene un item por su código.
   * @param codigo Código del item.
   */
  getById(codigo: string): Observable<ItemModel> {
    return this.http.get<ItemModel>(`${this.apiUrl}/${codigo}`);
  }

  /**
   * Crea un nuevo item.
   * @param pn Modelo de item a crear.
   */
  create(pn: ItemModel): Observable<ItemModel> {
    return this.http.post<ItemModel>(`${this.apiUrl}/add`, pn);
  }

  /**
   * Actualiza un item existente.
   * @param codigo Código del item a actualizar.
   * @param pn Modelo de item actualizado.
   */
  update(codigo: string, pn: ItemUpdateModel): Observable<ItemModel> {
    return this.http.put<ItemModel>(`${this.apiUrl}/update/${codigo}`, pn);
  }

  /**
   * Elimina un item por su código.
   * @param codigo Código del item a eliminar.
   */
  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}