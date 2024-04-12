import { Component, Output, EventEmitter, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import _ from '../images.download';
import * as jwt_decode from "jwt-decode";

type JwtPayloadWithRole = jwt_decode.JwtPayload & {
  role: string;
};
type Role = {
  authority: string
}
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private LOGIN_URL: string = "/api/user/signin";
  private IMAGE_URL: string = "/api/image/";
  private IMAGE_URL_ADMIN: string = "/api/image/admin";
  private ADMIN = "ROLE_ADMIN";
  protected isPopupShowing: boolean = false;
  protected popupMessage: string | undefined;

  @Input() token: string = "";

  @Output() tokenEvent = new EventEmitter<string>();
  @Output() loginEvent = new EventEmitter<boolean>();
  @Output() isLoggedInEvent = new EventEmitter<boolean>();
  @Output() emailEvent = new EventEmitter<string>();
  @Output() imagesEvent = new EventEmitter<any>();
  @Output() changeImagesEvent = new EventEmitter<string>();
  
  applyForm = new FormGroup({
    email: new FormControl(""),
    password: new FormControl("")
  })

  getRoleFromToken(token: string): string {
    const decodedToken = jwt_decode.jwtDecode(token);
    const asd = decodedToken as any;
    return asd.role.authority;
  }

  async onSubmit() {
    const email = this.applyForm.value.email;
    const password = this.applyForm.value.password;
    const response = await this.makeLoginRequest(email ?? '', password ?? '', this.LOGIN_URL);
    const headers: Headers = response.headers;
    const token = headers.get('authorization');
    let role: string = "";
    if (token) {
      localStorage.setItem('token', token);
      this.tokenEvent.emit(token);
      role = this.getRoleFromToken(token);
      //the following test line only works until this place, doesn't in the conditional branches it seems like!
    }
    if (!response.ok) {
      const body = await response.json();
      this.showPopup(body.reason);
    }
    else {
      this.loginEvent.emit(false);
      this.isLoggedInEvent.emit(true);
      this.emailEvent.emit(email ?? "");
      const currentUrl = role === this.ADMIN ? this.IMAGE_URL_ADMIN : this.IMAGE_URL + "/" + email;
      console.log(role);
      console.log(this.ADMIN);
      const images = _.downloadImages(currentUrl, token);
      console.log(currentUrl);
      this.imagesEvent.emit(images); 
    }
  }
  async makeLoginRequest(email: String, password: String, url: string): Promise<Response> {
    const response: Response = await fetch(url, {
      "method": "POST",
      "body": `{"email": "${email}", "password": "${password}"}`,
      "headers": { "Content-Type": "application/json" }
    })
    return response;
  }
  showPopup(message: string) {
    this.popupMessage = message;
    this.isPopupShowing = true;
    setTimeout(() => {
      this.isPopupShowing = false;
    }, 3000)
  }
}
