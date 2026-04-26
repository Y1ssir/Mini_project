// Badge.js — reusable colored badge for status and priority

import React from 'react';

const STATUS_MAP = {
  new:      'new',
  progress: 'progress',
  resolved: 'resolved',
  closed:   'closed',
};

const STATUS_LABEL = {
  new:      'New',
  progress: 'In Progress',
  resolved: 'Resolved',
  closed:   'Closed',
};

const PRIORITY_MAP = {
  high:   'high',
  medium: 'medium',
  low:    'low',
};

export function StatusBadge({ status }) {
  return (
    <span className={`badge ${STATUS_MAP[status] || ''}`}>
      {STATUS_LABEL[status] || status}
    </span>
  );
}

export function PriorityBadge({ priority }) {
  return (
    <span className={`badge ${PRIORITY_MAP[priority] || ''}`}>
      {priority.charAt(0).toUpperCase() + priority.slice(1)}
    </span>
  );
}
