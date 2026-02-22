import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { X, Plus, Trash2 } from "lucide-react";
import { createPost, updatePost } from "../../Services/AchievementService";
import { handleGlobalError } from "../../Services/GlobalExceptionService";

const CreatePostModal = ({ existingPost, onClose, onSuccess }) => {
  const isEditing = !!existingPost;
  const [tags, setTags] = useState(existingPost?.tags || []);
  const [tagInput, setTagInput] = useState("");
  const [attachments, setAttachments] = useState(
    existingPost?.attachmentUrls?.map((url) => ({ url, type: "OTHER" })) || []
  );
  const [submitting, setSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: {
      title: existingPost?.title || "",
      description: existingPost?.description || "",
    },
  });

  const addTag = () => {
    const trimmed = tagInput.trim().toLowerCase();
    if (trimmed && !tags.includes(trimmed)) {
      setTags((prev) => [...prev, trimmed]);
    }
    setTagInput("");
  };

  const removeTag = (tag) => setTags((prev) => prev.filter((t) => t !== tag));

  const addAttachment = () =>
    setAttachments((prev) => [...prev, { url: "", type: "IMAGE" }]);

  const updateAttachment = (index, field, value) => {
    setAttachments((prev) =>
      prev.map((att, i) => (i === index ? { ...att, [field]: value } : att))
    );
  };

  const removeAttachment = (index) =>
    setAttachments((prev) => prev.filter((_, i) => i !== index));

  const onSubmit = async (values) => {
    try {
      setSubmitting(true);
      const payload = {
        ...values,
        tags,
        attachments: attachments.filter((a) => a.url.trim() !== ""),
      };
      let response;
      if (isEditing) {
        response = await updatePost(existingPost.id, payload);
      } else {
        response = await createPost(payload);
      }
      onSuccess(response.data);
    } catch (e) {
      handleGlobalError(e);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg max-h-[90vh] overflow-y-auto">
        {/* Modal Header */}
        <div className="flex items-center justify-between p-5 border-b border-gray-100">
          <h2 className="text-xl font-bold text-gray-900">
            {isEditing ? "Edit Post" : "Create Achievement Post"}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-700 transition"
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="p-5 space-y-4">
          {/* Title */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Title <span className="text-red-500">*</span>
            </label>
            <input
              {...register("title", { required: "Title is required" })}
              className="border border-gray-300 rounded-lg px-3 py-2 w-full text-sm focus:outline-none focus:ring-2 focus:ring-black"
              placeholder="What's the achievement?"
            />
            {errors.title && (
              <p className="text-red-500 text-xs mt-1">{errors.title.message}</p>
            )}
          </div>

          {/* Description */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Description <span className="text-red-500">*</span>
            </label>
            <textarea
              {...register("description", {
                required: "Description is required",
              })}
              rows={4}
              className="border border-gray-300 rounded-lg px-3 py-2 w-full text-sm focus:outline-none focus:ring-2 focus:ring-black resize-none"
              placeholder="Tell everyone about it..."
            />
            {errors.description && (
              <p className="text-red-500 text-xs mt-1">
                {errors.description.message}
              </p>
            )}
          </div>

          {/* Tags */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Tags
            </label>
            <div className="flex gap-2 mb-2">
              <input
                value={tagInput}
                onChange={(e) => setTagInput(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && (e.preventDefault(), addTag())}
                className="border border-gray-300 rounded-lg px-3 py-2 flex-1 text-sm focus:outline-none focus:ring-2 focus:ring-black"
                placeholder="Add a tag and press Enter"
              />
              <button
                type="button"
                onClick={addTag}
                className="bg-black text-white px-3 py-2 rounded-lg text-sm hover:bg-gray-800 transition"
              >
                <Plus size={16} />
              </button>
            </div>
            <div className="flex flex-wrap gap-2">
              {tags.map((tag) => (
                <span
                  key={tag}
                  className="inline-flex items-center gap-1 bg-gray-100 text-gray-700 text-xs px-2 py-1 rounded-full"
                >
                  #{tag}
                  <button type="button" onClick={() => removeTag(tag)}>
                    <X size={10} />
                  </button>
                </span>
              ))}
            </div>
          </div>

          {/* Attachments — only on create */}
          {!isEditing && (
            <div>
              <div className="flex items-center justify-between mb-1">
                <label className="block text-sm font-medium text-gray-700">
                  Attachments
                </label>
                <button
                  type="button"
                  onClick={addAttachment}
                  className="text-xs text-black underline flex items-center gap-1"
                >
                  <Plus size={12} /> Add
                </button>
              </div>
              <div className="space-y-2">
                {attachments.map((att, index) => (
                  <div key={index} className="flex gap-2 items-center">
                    <input
                      value={att.url}
                      onChange={(e) =>
                        updateAttachment(index, "url", e.target.value)
                      }
                      className="border border-gray-300 rounded-lg px-3 py-1.5 flex-1 text-sm focus:outline-none"
                      placeholder="Attachment URL"
                    />
                    <select
                      value={att.type}
                      onChange={(e) =>
                        updateAttachment(index, "type", e.target.value)
                      }
                      className="border border-gray-300 rounded-lg px-2 py-1.5 text-sm focus:outline-none"
                    >
                      <option value="IMAGE">Image</option>
                      <option value="VIDEO">Video</option>
                      <option value="DOCUMENT">Document</option>
                      <option value="OTHER">Other</option>
                    </select>
                    <button
                      type="button"
                      onClick={() => removeAttachment(index)}
                      className="text-gray-400 hover:text-red-500 transition"
                    >
                      <Trash2 size={15} />
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Submit */}
          <button
            type="submit"
            disabled={submitting}
            className="w-full bg-black text-white font-medium py-2 rounded-lg hover:bg-gray-800 transition disabled:opacity-50"
          >
            {submitting
              ? isEditing
                ? "Saving..."
                : "Posting..."
              : isEditing
              ? "Save Changes"
              : "Post Achievement"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default CreatePostModal;
