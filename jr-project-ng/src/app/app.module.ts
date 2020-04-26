import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppComponent } from './app.component';
import {RouterModule} from '@angular/router';
import { MainComponent } from './main/main.component';
import { SearchComponent } from './search/search.component';
import { MngComponent } from './mng/mng.component';
import { LoginComponent } from './login/login.component';
import { EscapeHtmlPipe } from './pipes/keep-html.pipe';

@NgModule({
  declarations: [
    EscapeHtmlPipe,
    AppComponent,
    MainComponent,
    SearchComponent,
    MngComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    NgbModule,
    RouterModule.forRoot([
        {path: '', component: MainComponent}
        // , {path: 'login', component: LoginComponent}
        , {path: 'login/:objId', component: LoginComponent}
        , {path: 'mng', component: MngComponent}
        , {path: 'obj/:objId', component: MainComponent}
        , {path: 'search', component: SearchComponent}
    ]
      // ,{ enableTracing: true }
    )
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
  siteName = 'R-Project';
}
