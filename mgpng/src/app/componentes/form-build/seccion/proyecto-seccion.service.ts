import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

export interface ProyectoSeccion {
  id?: number;
  codigo?: string;
  usuarioCreacion?: string;
  fechaCreacion?: Date;
  usuarioModificacion?: string;
  fechaModificacion?: Date;
  codigoProyectoFK?: string;
  codigoSeccionFk?: string;
  orden?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProyectoSeccionService {
  private apiUrl = `${environment.appApiUrl}/proyecto-seccion`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<ProyectoSeccion[]> {
    return this.http.get<ProyectoSeccion[]>(`${this.apiUrl}/list`);
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

  getByCodigo(codigo: string): Observable<ProyectoSeccion> {
    return this.http.get<ProyectoSeccion>(`${this.apiUrl}/${codigo}`);
  }

  getByCodigoProyecto(codigoProyecto: string, params?: {
    page?: number;
    size?: number;
    sort?: string;
    direction?: string;
  }): Observable<any> {
    const queryParams: any = {};
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    queryParams.sort = params?.sort || 'orden';
    queryParams.direction = params?.direction || 'asc';

    return this.http.get<any>(`${this.apiUrl}/proyecto/${codigoProyecto}`, { params: queryParams });
  }

  create(proyectoSeccion: ProyectoSeccion): Observable<ProyectoSeccion> {
    return this.http.post<ProyectoSeccion>(`${this.apiUrl}/add`, proyectoSeccion);
  }

  update(codigo: string, proyectoSeccion: ProyectoSeccion): Observable<ProyectoSeccion> {
    return this.http.put<ProyectoSeccion>(`${this.apiUrl}/update/${codigo}`, proyectoSeccion);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
