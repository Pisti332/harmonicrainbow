import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AnalyzeService {

  constructor() { }
  async makeBrightnessAnalyticsRequest(email: string, nameOfImage: string, token: string) {
    const URL = "/api/analytics/brightness?email=" + email + "&name=" + nameOfImage;
     return await fetch(URL, {
      method: "GET",
      headers: {
        "Authorization": token
      }
    });
  }
  async makeColorCompositionRequest(email: string, nameOfImage: string, token: string) {
    const URL = "/api/analytics/color-composition?email=" + email + "&name=" + nameOfImage;
     return await fetch(URL, {
      method: "GET",
      headers: {
        "Authorization": token
      }
    });
  }
}
