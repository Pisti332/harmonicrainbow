import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EditService } from './edit.service';

@Component({
  selector: 'app-edit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit.component.html',
  styleUrl: './edit.component.css'
})
export class EditComponent {
  @Input() token: string = ''
  @Input() email: string | undefined;
  @Input() nameOfImage: string | undefined;

  @Input() image: Blob | undefined;
  @Output() imageChange = new EventEmitter<Blob>();
  constructor(private service: EditService) {

  }
  
  readonly blackAndWhiteURL: string = "api/service/black-and-white";
  readonly saturationChangeURL: string = "api/service/saturation-change";
  readonly changeColorURL: string = "api/service/change-color";

  colorChange: boolean = false;

  showColorChange() {
    this.colorChange = true;
  }
  async changeColorRange() {
    const body: any = {
      "from": {
        red: 0,
        green: 0,
        blue: 0
      },
      "to": {
        red: 0,
        green: 0,
        blue: 0
      },
      "newColor": {
        red: 0,
        green: 0,
        blue: 0
      }
    };
    const elem = document.getElementById("color-change-input-block");
    if (elem) {
      const chidren = elem.children;
      for (let i = 0; i < chidren.length; i++) {
        const child = chidren[i];
        if (child.tagName === "DIV") {
          for (let j = 0; j < child.children.length; j++) {
            const innerChild = child.children[j];
            if (innerChild.tagName === "INPUT") {
              const value = (<HTMLInputElement>innerChild).value;
              const channel = innerChild.id;
              const inputType = child.id;
              body[inputType][channel] = +value;
            }
          }
        }
      }
      const response = await this.service.makeColorChangeRequest(this.changeColorURL, this.nameOfImage, this.email, body, this.token);
      const image = await response.blob();
      this.imageChange.emit(image);
    }
  }

  saturationMenu: boolean = false;
  saturation: number = 0;
  openSaturation() {
    this.saturationMenu = true;
  }
  changeSaturation() {
    const saturation = (<HTMLInputElement>document.getElementById("saturationInput")).value;
    this.saturation = Number.parseInt(saturation);
  }
  async getSaturationChangedImage() {
    const image = await this.service.callSaturationChangeEndpoint(this.saturationChangeURL, this.nameOfImage, this.email, this.saturation, this.token);
    this.imageChange.emit(image);
  }

  value: number = 0;

  async getBlackAndWhite() {
    const url = this.blackAndWhiteURL + "?name=" + this.nameOfImage + "&email=" + this.email;
    const image = await this.service.fetchBlackAndWhite(url, this.token, this.image);
    this.imageChange.emit(image);
  }
}
