import React from 'react';
import { ExorcismChamber } from '../components/ExorcismChamber';

export function ExorcismPage({ mediums, spirits, onRefresh }) {
  return (
    <ExorcismChamber
      mediums={mediums}
      spirits={spirits}
      onRefresh={onRefresh}
    />
  );
}
