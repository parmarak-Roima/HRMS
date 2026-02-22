import axios from "axios";
import apiClient from "./apiClient";

const BASE = "/api/achievements";
const JSON_HEADERS = { headers: { "Content-Type": "application/json" } };

const unwrap = (res) => res;

// Feed
export const fetchFeed = (params = {}) =>
  apiClient(`${BASE}/feed`, { params }).then(unwrap);

// Posts
export const createPost = (data) =>
  apiClient.post(`${BASE}/posts`, data, JSON_HEADERS).then(unwrap);

export const updatePost = (postId, data) =>
  apiClient.put(`${BASE}/posts/${postId}`, data, JSON_HEADERS).then(unwrap);

export const deletePost = (postId, reason) =>
  apiClient.delete(`${BASE}/posts/${postId}`, { params: reason ? { reason } : {} }).then(unwrap);

// Likes
export const toggleLike = (postId) =>
  apiClient.post(`${BASE}/posts/${postId}/like`, null, JSON_HEADERS).then(unwrap);

// Comments
export const fetchComments = (postId) =>
  apiClient.get(`${BASE}/posts/${postId}/comments`).then(unwrap);

export const addComment = (postId, data) =>
  apiClient.post(`${BASE}/posts/${postId}/comments`, data, JSON_HEADERS).then(unwrap);

export const updateComment = (commentId, data) =>
  apiClient.put(`${BASE}/comments/${commentId}`, data, JSON_HEADERS).then(unwrap);

export const deleteComment = (commentId, reason) =>
  apiClient.delete(`${BASE}/comments/${commentId}`, { params: reason ? { reason } : {} }).then(unwrap);