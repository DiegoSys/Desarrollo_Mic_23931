import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DtoMatrizPoaConsulta, DtoPOA, DtoPOAUpdate } from './models/gestion-poa.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MatrizPoaConsultaService {
  private apiUrl = `${environment.appApiUrl}/poa`;

  constructor(private http: HttpClient) {}

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

  getById(id: number): Observable<DtoPOA> {
    return this.http.get<DtoPOA>(`${this.apiUrl}/${id}`);
  }

  create(dto: DtoPOA): Observable<DtoPOA> {
    return this.http.post<DtoPOA>(`${this.apiUrl}`, dto);
  }

  update(id: number, dto: DtoPOAUpdate): Observable<DtoPOA> {
    return this.http.put<DtoPOA>(`${this.apiUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}