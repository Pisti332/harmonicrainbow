import { Component, Input, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

enum DescriptionState {
  ABOUT = 'Harmonicrainbow provides you advanced image analytics and editing on server side.',
  ANALYZE = 'Getting details from images help you categorize and understand your images better.',
  EDIT = "Editing images by the basic and more advanced aspects than you'r familiar with, all on the server side. This can be beneficial with lower end computers.",
}
@Component({
  selector: 'app-description',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './description.component.html',
  styleUrl: './description.component.css'
})
export class DescriptionComponent {
  @Input() descriptionState: string | null = null;
  readonly ABOUT: string = DescriptionState.ABOUT;
  readonly ANALYZE: string = DescriptionState.ANALYZE;
  readonly EDIT: string = DescriptionState.EDIT;

  description: string = '';
  
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['descriptionState']) {
      this.setDescription();
    }
  }
  
  setDescription(): void {
    if (this.descriptionState === "about") {
      this.description = this.ABOUT;
    }
    else if (this.descriptionState === "edit") {
      this.description = this.EDIT;
    }
    else if (this.descriptionState === "analyze") {
      this.description = this.ANALYZE;
    }
  }
}
