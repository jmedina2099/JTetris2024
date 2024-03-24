import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BoxComponent } from './components/box/box.component';
import { FigureComponent } from './components/figure/figure.component';
import { VentanaPrincipalComponent } from './components/ventana-principal/ventana-principal.component';

@NgModule({
  declarations: [
    AppComponent,
    VentanaPrincipalComponent,
    FigureComponent,
    BoxComponent,
  ],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
