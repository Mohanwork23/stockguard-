// Week 2, Day 13-14 deliverable.
// Goal: prove StockGuard never oversells stock under concurrent load.
//
// Setup: seed a product with availableStock = 50, then fire far more than
// 50 concurrent order requests for quantity=1 each. Count how many succeed
// (should be exactly 50) vs. fail with 409/InsufficientStock (the rest).
// Run this against BOTH the optimistic and pessimistic endpoints and
// record throughput + latency for the README comparison table.
//
// Install k6: https://k6.io/docs/get-started/installation/
// Run:  k6 run loadtest/order_concurrency_test.js

import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 100,        // 100 virtual users
  iterations: 100,  // each fires once -> 100 concurrent attempts on 50 units of stock
};

export default function () {
  const payload = JSON.stringify({ productId: 1, quantity: 1 });
  const params = { headers: { 'Content-Type': 'application/json' } };

  // Use ?strategy=pessimistic to compare the pessimistic lock endpoint:
  // http://localhost:8080/api/orders?strategy=pessimistic
  const res = http.post('http://localhost:8080/api/orders', payload, params);

  check(res, {
    'status is 201 or 400 (expected outcomes only)': (r) =>
      r.status === 201 || r.status === 400,
  });
}

// After running, tally results manually or extend this script to write
// success/failure counts to a summary file. Expected: exactly 50 succeed.
