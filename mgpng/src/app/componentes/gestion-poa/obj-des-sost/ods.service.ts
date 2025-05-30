import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OdsModel, OdsUpdateModel } from './models/obj-des-sost.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OdsService {
  private apiUrl = `${environment.appApiUrl}/objdessost`;

  constructor(private http: HttpClient) {}

  //debe mandar los parametros que se obtienen desde html no con valores quemados
    getAll(params?: { page?: number; size?: number; sort?: string }): Observable<any> {
    // Valores predeterminados
    const defaultParams = {
      page: '0',
      size: '10',
    };
  
    // Combina los valores predeterminados con los parámetros proporcionados
    const queryParams = {
      ...defaultParams,
      ...params
    };

    return this.http.get<any>(this.apiUrl, { params: queryParams });
  }

  getById(codigo: string): Observable<OdsModel> {
    return this.http.get<OdsModel>(`${this.apiUrl}/${codigo}`);
  }

  create(ods: OdsModel): Observable<OdsModel> {
    return this.http.post<OdsModel>(`${this.apiUrl}/add`, ods);
  }

  update(codigo: string, ods: OdsUpdateModel): Observable<OdsModel> {
    return this.http.put<OdsModel>(`${this.apiUrl}/update/${codigo}`, ods);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
