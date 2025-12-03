import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

interface Participant {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  status: string;
}

@Component({
  selector: 'app-participant-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './participant-list.component.html',
  styleUrl: './participant-list.component.css',
})
export class ParticipantListComponent implements OnInit {
  private http = inject(HttpClient);
  participants: Participant[] = [];

  ngOnInit(): void {
    this.http
      .get<Participant[]>('http://localhost:9040/api/participants')
      .subscribe((data) => {
        this.participants = data as Participant[];
      });
  }
}


