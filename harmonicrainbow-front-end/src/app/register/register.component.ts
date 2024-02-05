import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  private REGISTER_URL: string = "/api/user/signup";
  public isPopupShowing: boolean = false;
  popupMessage: string = "";

  @Output() registerEvent = new EventEmitter<boolean>();

  applyForm = new FormGroup({
    email: new FormControl(""),
    password: new FormControl("")
  })

  async onSubmit() {
    const email = this.applyForm.value.email;
    const password = this.applyForm.value.password;
    const response = await this.makeRegisterRequest(email ?? '', password ?? '', this.REGISTER_URL);
    const body = await response.json();
    this.showPopup(body.reason);
  }
  async makeRegisterRequest(email: String, password: String, url: string): Promise<Response> {
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
