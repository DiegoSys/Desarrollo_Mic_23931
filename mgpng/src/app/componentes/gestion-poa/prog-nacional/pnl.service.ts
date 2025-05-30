import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PnlModel, PnlUpdateModel } from './models/pnl.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PnlService {
private apiUrl = `${environment.appApiUrl}/prognacional`;
  constructor(private http: HttpClient) {}

  getAll(params?: { 
    page?: number;
    size?: number;
    sort?: string;
    order?: string;
    totalRecords?: number
  }): Observable<any> {
    const queryParams: any = {};
    
    queryParams.page = (params?.page ?? 0).toString();
    queryParams.size = (params?.size ?? 10).toString();
    
    if (params?.sort) {
      queryParams.sort = params.sort;
      queryParams.order = params.order || 'asc';
    }

    if (params?.totalRecords !== undefined) {
      queryParams.totalRecords = params.totalRecords.toString();
    }

    return this.http.get<any>(this.apiUrl, { params: queryParams });
  }

  getById(codigo: string): Observable<PnlModel> {
    return this.http.get<PnlModel>(`${this.apiUrl}/${codigo}`);
  }

  create(pn: PnlModel): Observable<PnlModel> {
    return this.http.post<PnlModel>(`${this.apiUrl}/add`, pn);
  }

  update(codigo: string, pn: PnlUpdateModel): Observable<PnlModel> {
    return this.http.put<PnlModel>(`${this.apiUrl}/update/${codigo}`, pn);
  }

  delete(codigo: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${codigo}`);
  }
}
