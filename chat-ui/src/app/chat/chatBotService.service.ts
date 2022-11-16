import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatBotServiceService {

  public readonly chatBotUrl = "http://localhost:8080/api/chat";

  constructor(private http: HttpClient) { }

  public sendMessage(texto: string, message: any): Observable<any[]> {

    return this.http.post<any[]>(this.chatBotUrl + `?texto=${texto}`, message);
  }

}
