import { Component } from '@angular/core';
import { ContactForm } from '../../components/contactComponents/contact-form/contact-form';

@Component({
  selector: 'app-contact',
  imports: [ContactForm],
  templateUrl: './contact.html',
  styleUrl: './contact.css'
})
export class Contact {

}
