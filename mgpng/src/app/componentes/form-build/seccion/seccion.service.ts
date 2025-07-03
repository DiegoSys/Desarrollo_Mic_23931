import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { Seccion } from "../models/form-build.model";

@Injectable({
  providedIn: 'root'
})
export class SeccionService {
  private apiUrl = `${environment.appApiUrl}/seccion`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Seccion[]> {
    return this.http.get<Seccion[]>(`${this.apiUrl}/list`);
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

  getByCodigo(codigo: string): Observable<Seccion> {
    return this.http.get<Seccion>(`${this.apiUrl}/${codigo}`);
  }

  create(seccion: Seccion): Observable<Seccion> {
    return this.http.post<Seccion>(`${this.apiUrl}/add`, seccion);
  }

  update(codigo: string, seccion: Seccion): Observable<Seccion> {
    return this.http.put<Seccion>(`${this.apiUrl}/update/${codigo}`, seccion);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}



