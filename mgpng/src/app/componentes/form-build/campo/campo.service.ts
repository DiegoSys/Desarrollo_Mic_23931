import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { Campo } from "../models/form-build.model";

@Injectable({
  providedIn: 'root'
})
export class CampoService {
  private apiUrl = `${environment.appApiUrl}/campo`;

  constructor(private http: HttpClient) {}

  getAllPaginated(params?: {
    page?: number;
    size?: number;
    sort?: string;
    direction?: string;
    searchCriteria?: { [key: string]: string }
  }): Observable<any> {
    let queryParams = new HttpParams()
      .set('page', (params?.page ?? 0).toString())
      .set('size', (params?.size ?? 10).toString())
      .set('sort', params?.sort || 'fechaCreacion')
      .set('direction', params?.direction || 'desc');
    if (params?.searchCriteria) {
      Object.entries(params.searchCriteria).forEach(([key, value]) => {
        queryParams = queryParams.set(key, value);
      });
    }
    return this.http.get<any>(`${this.apiUrl}`, { params: queryParams });
  }

  getByCodigo(codigo: string): Observable<Campo> {
    return this.http.get<Campo>(`${this.apiUrl}/${codigo}`);
  }

  create(campo: Campo): Observable<Campo> {
    return this.http.post<Campo>(`${this.apiUrl}/add`, campo);
  }

  update(codigo: string, campo: Campo): Observable<Campo> {
    return this.http.put<Campo>(`${this.apiUrl}/update/${codigo}`, campo);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }


}
