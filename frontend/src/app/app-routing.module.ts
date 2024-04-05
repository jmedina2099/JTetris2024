import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { VentanaPrincipalComponent } from './components/ventana-principal/ventana-principal.component';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'home' },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  {
    path: 'jtetris',
    component: VentanaPrincipalComponent,
    data: {
      figureOperation: {
        figure: {},
        initialTimeStamp: 0,
        timeStamp: 0,
        operation: undefined,
      },
      boardOperation: {
        boxes: undefined,
        timeStamp: 0,
        operation: undefined,
      },
      waitForBottomDown: {
        waiting: false,
      },
      game: {
        isRunning: false,
      },
    },
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
