import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppComponent } from './app.component';
import {RouterModule} from '@angular/router';
import { MainComponent } from './main/main.component';
import { SearchComponent } from './search/search.component';
import { MngComponent } from './mng/mng.component';

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    SearchComponent,
    MngComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    NgbModule,
    RouterModule.forRoot([
        {path: '', component: MainComponent}
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
