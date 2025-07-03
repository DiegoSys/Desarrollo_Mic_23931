import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

export interface SeccionCampo {
  id?: number;
  codigo?: string;
  orden?: number;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string;
  fechaModificacion?: Date;
  codigoSeccionFk?: string;
  codigoCampoFk?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SeccionCampoService {
  private apiUrl = `${environment.appApiUrl}/seccion-campo`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<SeccionCampo[]> {
    return this.http.get<SeccionCampo[]>(`${this.apiUrl}/list`);
  }

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

  getByCodigo(codigo: string): Observable<SeccionCampo> {
    return this.http.get<SeccionCampo>(`${this.apiUrl}/${codigo}`);
  }

  getByCodigoSeccion(codigoSeccion: string, params?: {
    page?: number;
    size?: number;
    sort?: string;
    direction?: string;
  }): Observable<any> {
    const queryParams: any = {};
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    queryParams.sort = params?.sort || 'fechaCreacion';
    queryParams.direction = params?.direction || 'asc';

    return this.http.get<any>(`${this.apiUrl}/seccion/${codigoSeccion}`, { params: queryParams });
  }

  getByCodigoCampo(codigoCampo: string, params?: {
    page?: number;
    size?: number;
    sort?: string;
    direction?: string;
  }): Observable<any> {
    const queryParams: any = {};
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    queryParams.sort = params?.sort || 'fechaCreacion';
    queryParams.direction = params?.direction || 'asc';

    return this.http.get<any>(`${this.apiUrl}/campo/${codigoCampo}`, { params: queryParams });
  }

  create(seccionCampo: SeccionCampo): Observable<SeccionCampo> {
    return this.http.post<SeccionCampo>(`${this.apiUrl}/add`, seccionCampo);
  }

  update(codigo: string, seccionCampo: SeccionCampo): Observable<SeccionCampo> {
    return this.http.put<SeccionCampo>(`${this.apiUrl}/update/${codigo}`, seccionCampo);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}