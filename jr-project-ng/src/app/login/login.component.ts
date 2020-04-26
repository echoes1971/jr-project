import { Component, OnInit } from '@angular/core';
import {CoreService} from '../core.service';
import {Router, ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private coreService: CoreService, private route: ActivatedRoute, private router: Router) { }

  model = { login: '', pwd: '' };

  ngOnInit(): void {
  }

  onSubmit() {
    this.coreService.login(this.model.login, this.model.pwd);

    // console.log(this.router.url);
    // console.log(this.router.url.split('/'));
    const tmp = this.router.url.split('/');
    this.router.navigateByUrl('/obj/' + tmp[tmp.length - 1]);
  }

  // TODO: Remove this when we're done
  // get diagnostic() { return JSON.stringify(this.model); }
}
