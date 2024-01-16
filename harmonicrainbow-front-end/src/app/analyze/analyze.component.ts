import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AnalyzeService } from './analyze.service';

@Component({
  selector: 'app-analyze',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './analyze.component.html',
  styleUrl: './analyze.component.css'
})
export class AnalyzeComponent {
  @Input() email: string = "";
  @Input() nameOfImage: string = "";
  @Input() token: string = '';
  inputValue: number = 0;
  brightness: number | null = null;
  colorComposition: number[] | null = null;
  constructor(private service: AnalyzeService) {

  }

  setValue(event: Event) {
    this.inputValue = Number.parseInt((event.target as HTMLInputElement).value);
  }
  async getBrightness() {
    const response = await this.service.makeBrightnessAnalyticsRequest(this.email, this.nameOfImage, this.token);
    const body = await response.json();
    this.brightness = body["brightness"];
  }
  async getColorComposition() {
    const response = await this.service.makeColorCompositionRequest(this.email, this.nameOfImage, this.token);
    const body = await response.json();
    const red = body["red"];
    const green = body["green"];
    const blue = body["blue"];
    this.colorComposition = [red, green, blue];
  }
}
