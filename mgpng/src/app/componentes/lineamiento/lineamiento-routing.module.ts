import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  // Aquí defines solo las rutas propias de lineamiento, no módulos hijos
  {
    path: 'operativo',
    loadChildren: () =>
      import('./operativo/operativo.module').then(m => m.OperativoModule)
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LineamientoRoutingModule {}