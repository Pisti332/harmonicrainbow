import { Component } from '@angular/core';
import { CommonModule, JsonPipe } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { DescriptionComponent } from "./description/description.component"
import { AnalyzeComponent } from './analyze/analyze.component';
import { EditComponent } from './edit/edit.component';
import { OnInit } from '@angular/core';
import _ from './images.download';
import * as jwt_decode from "jwt-decode";

type Image = { name: string, userId: string };
type JwtPayloadWithRole = jwt_decode.JwtPayload & {
    role: Role;
};
type Role = {authority: string};
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, LoginComponent,
    RegisterComponent, DescriptionComponent, AnalyzeComponent,
    EditComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  IMAGE_URL = "/api/image";
  IMAGE_URL_ADMIN = "/api/image/admin";
  ADMIN = "ROLE_ADMIN";

  title = 'harmonicrainbow-front-end';
  login: boolean = false;
  register: boolean = false;
  isLoggedIn: boolean = false;
  token: string = '';
  email: string = '';
  imageUserId: string = '';
  role?: Role;
  images: Array<Image> = [];
  descriptionState: string | null = null;
  isAnalyzeWorkbench: boolean = false;
  isEditWorkbench: boolean = false;
  currentImageURL: string = "assets/img/root-template.jpg";
  currentImageName: string = "";
  page: number = 0;
  blob: Blob | undefined;

  decodeToken(token: string) {
    const decodedToken = jwt_decode.jwtDecode(token) as JwtPayloadWithRole;
    this.email = decodedToken.sub ? decodedToken.sub : "";
    this.role = decodedToken.role ? decodedToken.role : {authority: ""};
  }

  async ngOnInit() {
    const token = localStorage.getItem('token');
    if (token) {
      this.token = token;
      this.setIsLoggedIn(true);
      this.decodeToken(token); 
      const currentUrl = this.role?.authority === this.ADMIN ? this.IMAGE_URL_ADMIN : this.IMAGE_URL + "/" + this.email;

      const response = await _.downloadImages(currentUrl, this.token);
      this.images = await response.json();
      this.changeImages("");
    }
  }

  setToken(token: string) {
    this.token = token;
  }

  updateImage(image: Blob) {
    this.currentImageURL = URL.createObjectURL(image);
  }

  test(str: string) {
    console.log(str);
  }

  async changeImages(direction: string) {
    if (direction === "L" && this.page !== 0) {
      this.page--;
    }
    else if (direction === "R" && this.page !== this.images.length - 1) {
      this.page++;
    }
    this.imageUserId = this.images[this.page].userId;
    const image = await _.downloadImage(this.IMAGE_URL, this.imageUserId, this.images[this.page].name, this.token);
    const blob = await image.blob();
    this.blob = blob;
    this.currentImageName = this.images[this.page].name;
    this.currentImageURL = URL.createObjectURL(blob);
  }

  async setImages(images: any) {
    const response = await images;
    const body = await response.json();
    this.images = body;
    await this.changeImages("");
  }

  setEmail(email: string) {
    this.email = email;
  }
  disableLogin() {
    this.login = false;
  }
  triggerLogin(): void {
    this.login = true;
  }
  triggerRegister(): void {
    this.register = true;
  }
  hidePopUps(): void {
    this.login = false;
    this.register = false;
  }
  setIsLoggedIn(isLoggedIn: boolean): void {
    this.isLoggedIn = isLoggedIn;
  }
  logout(): void {
    this.token = '';
    this.setIsLoggedIn(false);
    localStorage.removeItem('token');
  }
  async uploadImage(event: any): Promise<void> {
    const file: File = event.target.files[0];
    if (file) {
      const formData = new FormData();
      formData.append("image", file);
      formData.append("email", this.email);
      await fetch(this.IMAGE_URL, {
        method: "POST",
        "headers": {
          "Authorization": this.token
        },
        "body": formData
      });
      const currentUrl = this.role?.authority === this.ADMIN ? this.IMAGE_URL_ADMIN : this.IMAGE_URL + "/" + this.email;
      const response = await _.downloadImages(currentUrl, this.token);
      this.images = await response.json();
    }
  }
  setDescription(state: string | null): void {
    if (this.isLoggedIn && state == 'analyze') {
      this.isEditWorkbench = false;
      this.isAnalyzeWorkbench = true;
    }
    else if (this.isLoggedIn && state == 'edit') {
      this.isAnalyzeWorkbench = false;
      this.isEditWorkbench = true;
    }
    else {
      this.descriptionState = state;
    }
  }
  disableWorkbenches() {
    this.isAnalyzeWorkbench = false;
    this.isEditWorkbench = false;
  }
  hideEditWorkbench() {
    this.isEditWorkbench = false;
  }
}