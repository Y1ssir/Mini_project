export const MOCK_INCIDENTS = [
  { id: 'INC-001', title: 'Printer not working on floor 2', priority: 'high', status: 'progress', department: 'IT', createdAt: '2026-03-16', assignedTo: 'Karim T.' },
  { id: 'INC-002', title: 'Cannot connect to VPN', priority: 'medium', status: 'new', department: 'IT', createdAt: '2026-03-15', assignedTo: null },
  { id: 'INC-003', title: 'Keyboard not working', priority: 'low', status: 'resolved', department: 'IT', createdAt: '2026-03-10', assignedTo: 'Salma A.' },
  { id: 'INC-004', title: 'Need access to Financial Server', priority: 'high', status: 'new', department: 'Finance', createdAt: '2026-04-20', assignedTo: null },
  { id: 'INC-005', title: 'Monitor is flickering', priority: 'low', status: 'progress', department: 'HR', createdAt: '2026-04-21', assignedTo: 'Karim T.' },
  { id: 'INC-006', title: 'Lost office keycard', priority: 'high', status: 'closed', department: 'Operations', createdAt: '2026-02-01', assignedTo: 'Salma A.' },
];

export const MOCK_USERS = [
  { id: 'U-01', name: 'Karim T.', email: 'karim@company.com', role: 'Admin', status: 'Active' },
  { id: 'U-02', name: 'Salma A.', email: 'salma@company.com', role: 'Technician', status: 'Active' },
  { id: 'U-03', name: 'Ali Hassan', email: 'ali@company.com', role: 'User', status: 'Inactive' },
];
