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
  colorComposition: string | null = null;
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
  async getColorComposition(): Promise<number[]> {
    const response = await this.service.makeColorCompositionRequest(this.email, this.nameOfImage, this.token);
    const body = await response.json();
    const red = body["red"];
    const green = body["green"];
    const blue = body["blue"];
    return [red, green, blue];
  }
  async formatColorComposition() {
    const colors = await this.getColorComposition();
    const colorCompositionAsText = "Red: " + colors[0] + "%  Green: " +
    colors[1] + "% Blue: " + colors[2] + "%";
    this.colorComposition = colorCompositionAsText;
  }
}
