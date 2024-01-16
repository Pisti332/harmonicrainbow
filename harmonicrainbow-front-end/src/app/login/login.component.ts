import { Component, Output, EventEmitter, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import _ from './images.download';

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
  @Input() token: string = "";
  public invalidCredentialsPopup: boolean = false;

  @Output() tokenEvent = new EventEmitter<string>();
  @Output() loginEvent = new EventEmitter<boolean>();
  @Output() isLoggedInEvent = new EventEmitter<boolean>();
  @Output() emailEvent = new EventEmitter<string>();
  @Output() imagesEvent = new EventEmitter<any>();

  applyForm = new FormGroup({ 
    email: new FormControl(""),
    password: new FormControl("")
  })

  async onSubmit() {
    const email = this.applyForm.value.email;
    const password = this.applyForm.value.password;
    const response = await this.makeLoginRequest(email ?? '', password ?? '', this.LOGIN_URL);
    const headers: Headers = response.headers;
    const token = headers.get('authorization');
    if (token) {
      this.tokenEvent.emit(token);
    }
    if (!response.ok) {
      this.showInvalidCredentialsPopup();
      throw new Error("Login wasn't successful!");
    }
    else {
      this.loginEvent.emit(false);
      this.isLoggedInEvent.emit(true);
      if (email) {
        this.emailEvent.emit(email);
        const images = _.downloadImages(this.IMAGE_URL, email, token);
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
  showInvalidCredentialsPopup() {
    this.invalidCredentialsPopup = true;
    setTimeout(() => {
      // var element = document.getElementById('invalidCredentialsPopup')!;
      // element.classList.toggle('fade-out');
      this.invalidCredentialsPopup = false;
    }, 3000)
  }
}
