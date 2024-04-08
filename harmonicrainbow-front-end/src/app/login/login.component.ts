import { Component, Output, EventEmitter, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import _ from '../images.download';
import * as jwt_decode from "jwt-decode";

type JwtPayloadWithRole = jwt_decode.JwtPayload & {
  role: string;
};
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
  private IMAGE_URL_ADMIN: string = "/api/image/admin/";
  @Input() token: string = "";
  protected isPopupShowing: boolean = false;
  protected popupMessage: string | undefined;
  ADMIN = "ADMIN_ROLE";

  @Output() tokenEvent = new EventEmitter<string>();
  @Output() loginEvent = new EventEmitter<boolean>();
  @Output() isLoggedInEvent = new EventEmitter<boolean>();
  @Output() emailEvent = new EventEmitter<string>();
  @Output() imagesEvent = new EventEmitter<any>();

  applyForm = new FormGroup({
    email: new FormControl(""),
    password: new FormControl("")
  })

  getRoleFromToken(token: string): string {
    const decodedToken = jwt_decode.jwtDecode(token) as JwtPayloadWithRole;
    return decodedToken.role ? decodedToken.role : "";
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
    }
    const body = await response.json();
    if (!response.ok) {
      this.showPopup(body.reason);
    }
    else {
      this.loginEvent.emit(false);
      this.isLoggedInEvent.emit(true);
      if (email) {
        this.emailEvent.emit(email);
        const currentUrl = role?.includes(this.ADMIN) ? this.IMAGE_URL_ADMIN : this.IMAGE_URL + "/" + email;
        const images = _.downloadImages(currentUrl, token);
        this.imagesEvent.emit(images);
      }
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
