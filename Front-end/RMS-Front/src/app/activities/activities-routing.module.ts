import { ActivityDetailComponent } from './activity-detail';
import { EditActivityComponent } from './edit-activity/edit-activity.component';
import { AddActivityComponent } from './add-activity';
import { ActivitiesListComponent } from './activities-list';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeatureActivityListComponent } from './feature-activity-list/feature-activity-list.component';
import { FeatureActivityListDeactivate } from './feature-activity-list/feature-activity-list.deactivate';

const routes: Routes = [
  {
    path: '',
    component: FeatureActivityListComponent,
    canDeactivate: [FeatureActivityListDeactivate],
    data: {
      breadcrumb: 'List activity',
    },
  },
  {
    path: 'add',
    component: AddActivityComponent,
  },
  {
    path: 'add/:id',
    component: AddActivityComponent,
  },
  {
    path: 'edit/:id',
    component: EditActivityComponent,
  },
  {
    path: 'detail/:id',
    component: ActivityDetailComponent,
  },
  {
    path: 'old',
    component: ActivitiesListComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ActivitiesRoutingModule {}
