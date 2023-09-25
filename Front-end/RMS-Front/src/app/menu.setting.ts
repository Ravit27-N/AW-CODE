import { Access } from './auth';

export interface MenuNode {
  name: string;
  icon?: string;
  link?: string;
  children?: MenuNode[];
  perm?: string;
}

export const MENU_DATA: MenuNode[] = [
  {
    name: 'Dashboard',
    icon: 'dashboard',
    link: '/admin/dashboard',
  },
  { name: 'Calendar', icon: 'event_available', link: '/admin/calendar' },
  {
    name: 'Interview',
    icon: 'history_toggle_off',
    link: '/admin/interview',
    perm: Access.interview,
  },
  {
    name: 'Candidate',
    icon: 'person_add',
    link: '/admin/candidate',
    perm: Access.candidate,
  },
  {
    name: 'Demand',
    icon: 'auto_graph',
    link: '/admin/demand',
    perm: Access.demand,
  },
  {
    name: 'Report',
    icon: 'bar_chart',
    link: '/admin/candidate/report',
    perm: Access.candidate,
  },
  {
    name: 'Advance Report',
    icon: 'query_stats',
    link: '/admin/candidate/advance-report',
    perm: Access.candidate,
  },
  {
    name: 'Activity',
    icon: 'list_alt',
    link: '/admin/activities',
    perm: Access.activity,
  },
  {
    name: 'Reminder',
    icon: 'notification_important',
    link: '/admin/reminders',
    perm: Access.reminder,
  },
  {
    name: 'Setting',
    icon: 'settings',
    children: [
      {
        name: 'Company Profile',
        icon: 'home_work',
        link: '/admin/setting/feature-company-profile',
        perm: Access.company,
      },
      {
        name: 'File Manager',
        icon: 'snippet_folder',
        link: '/admin/setting/file-manager',
      },
      {
        name: 'Interview Template',
        icon: 'rule',
        link: '/admin/setting/interviewtemplate',
        perm: Access.interview,
      },
      {
        name: 'Job',
        icon: 'hail',
        link: '/admin/setting/job',
        perm: Access.job,
      },
      {
        name: 'Project',
        icon: 'assignment_ind',
        link: '/admin/setting/projects',
        perm: Access.project,
      },
      {
        name: 'Mail Configuration',
        icon: 'mark_email_read',
        link: '/admin/setting/mail-configuration',
        perm: Access.mail,
      },
      {
        name: 'Mail Template',
        icon: 'view_quilt',
        link: '/admin/setting/mailtemplate',
        perm: Access.template,
      },
      {
        name: 'System Configuration',
        icon: 'settings_applications',
        link: '/admin/setting/system-config',
        perm: Access.system,
      },
      {
        name: 'Status Candidate',
        icon: 'how_to_reg',
        link: '/admin/setting/statuscandidate',
        perm: Access.status,
      },
      { name: 'University', icon: 'school', link: '/admin/setting/university' },
    ],
  },
  {
    name: 'Administration',
    icon: 'admin_panel_settings',
    children: [
      {
        name: 'User',
        icon: 'people_outline',
        link: '/admin/administration/users',
        perm: Access.user,
      },
      {
        name: 'Role',
        icon: 'policy',
        link: '/admin/administration/roles',
        perm: Access.user,
      },
      {
        name: 'Group',
        icon: 'groups',
        link: '/admin/administration/groups',
        perm: Access.user,
      },
    ],
  },
];
