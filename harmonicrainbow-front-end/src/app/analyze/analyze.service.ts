import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AnalyzeService {

  constructor() { }
  async makeBrightnessAnalyticsRequest(userId: string, nameOfImage: string, token: string) {
    const URL = "/api/analytics/brightness?userId=" + userId + "&name=" + nameOfImage;
     return await fetch(URL, {
      method: "GET",
      headers: {
        "Authorization": token
      }
    });
  }
  async makeColorCompositionRequest(userId: string, nameOfImage: string, token: string) {
    const URL = "/api/analytics/color-composition?userId=" + userId + "&name=" + nameOfImage;
     return await fetch(URL, {
      method: "GET",
      headers: {
        "Authorization": token
      }
    });
  }
}
