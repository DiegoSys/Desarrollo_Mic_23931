import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TipoProyecto } from './models/form-build.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FormBuildListService {
  private apiUrl = `${environment.appApiUrl}/tipoproyecto`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<TipoProyecto[]> {
    return this.http.get<TipoProyecto[]>(`${this.apiUrl}/list`);
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

  getByCodigo(codigo: string): Observable<TipoProyecto> {
    return this.http.get<TipoProyecto>(`${this.apiUrl}/${codigo}`);
  }

  create(tipoProyecto: TipoProyecto): Observable<TipoProyecto> {
    return this.http.post<TipoProyecto>(`${this.apiUrl}/add`, tipoProyecto);
  }

  update(codigo: string, tipoProyecto: TipoProyecto): Observable<TipoProyecto> {
    return this.http.put<TipoProyecto>(`${this.apiUrl}/update/${codigo}`, tipoProyecto);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}