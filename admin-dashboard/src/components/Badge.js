import React from 'react';

export function StatusBadge({ status }) {
  return <span className={`badge ${status}`}>{status}</span>;
}

export function PriorityBadge({ priority }) {
  return <span className={`badge ${priority}`}>{priority}</span>;
}
