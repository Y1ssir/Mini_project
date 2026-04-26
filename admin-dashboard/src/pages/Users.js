import React from 'react';
import { MOCK_USERS } from '../mockData';

export default function Users() {
  return (
    <div>
      <div className="page-header">
        <h1>User Management</h1>
        <button className="btn primary">+ Add User</button>
      </div>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {MOCK_USERS.map(u => (
              <tr key={u.id}>
                <td><span className="tag-id">{u.id}</span></td>
                <td>{u.name}</td>
                <td>{u.email}</td>
                <td>{u.role}</td>
                <td>
                  <span className={`badge ${u.status === 'Active' ? 'resolved' : 'closed'}`}>
                    {u.status}
                  </span>
                </td>
                <td>
                  <button className="btn sm">Edit</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
