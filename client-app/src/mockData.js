// mockData.js
// This is fake data so you can see the UI working without a backend
// When the backend is ready, delete this file and use api.js instead

export const MOCK_INCIDENTS = [
  {
    id: 'INC-001',
    title: 'Printer not working on floor 2',
    description: 'The HP printer is not printing. Jobs are sent but nothing comes out.',
    priority: 'high',
    status: 'progress',
    createdAt: '2026-03-16',
    assignedTo: 'Karim T.',
  },
  {
    id: 'INC-002',
    title: 'Cannot connect to VPN',
    description: 'VPN keeps disconnecting every 5 minutes.',
    priority: 'medium',
    status: 'new',
    createdAt: '2026-03-15',
    assignedTo: null,
  },
  {
    id: 'INC-003',
    title: 'Keyboard not working',
    description: 'Some keys on the keyboard are stuck.',
    priority: 'low',
    status: 'resolved',
    createdAt: '2026-03-10',
    assignedTo: 'Salma A.',
  },
];

export const MOCK_COMMENTS = [
  { id: 1, incidentId: 'INC-001', author: 'Karim T.', text: 'Checked remotely — print spooler is stuck. Restarting now.', createdAt: '2026-03-16 10:32' },
  { id: 2, incidentId: 'INC-001', author: 'Ali Hassan', text: 'It works now, thank you!', createdAt: '2026-03-16 10:45' },
];
