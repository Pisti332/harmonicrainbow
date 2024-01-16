import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class EditService {

  constructor() { }
  async callSaturationChangeEndpoint(saturationChangeURL: string,
    nameOfImage: string | undefined, email: string | undefined,
    saturation: number, token: string) {
    const url = saturationChangeURL + "?name=" + nameOfImage + "&email=" + email +
     "&saturation=" + saturation;
    const response = await fetch(url, {
      method: "GET",
      headers: {
        "Authorization": token
      }
    })
    return await response.blob();
  }
  async fetchBlackAndWhite(url: string, token: string, image: Blob | undefined) {
    const response = await fetch(url, {
      method: "POST",
      headers: {
        "Authorization": token
      },
      body: image
    })
    return await response.blob();
  }
  async makeColorChangeRequest(changeColorURL: string, nameOfImage: string | undefined, email: string | undefined, body: string, token: string) {
    const url = changeColorURL + "?name=" + nameOfImage + "&email=" + email;
    return await fetch(url, {
      method: "POST",
      headers: {
        "Authorization": token,
        "Content-Type": "application/json"
      },
      body: JSON.stringify(body)
    })
  }
}
