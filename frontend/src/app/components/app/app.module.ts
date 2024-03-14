import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { VentanaPrincipalComponent } from '../ventana-principal/ventana-principal.component';
import { AppComponent } from './app.component';
import { FigureComponent } from '../figure/figure.component';

@NgModule({
  declarations: [AppComponent, VentanaPrincipalComponent, FigureComponent],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
