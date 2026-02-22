import React from "react";
import { SlidersHorizontal, X } from "lucide-react";

const FilterBar = ({ filters, setFilters }) => {
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const resetFilters = () => {
    setFilters({ authorId: "", tagName: "", from: "", to: "" });
  };

  const hasActiveFilters = Object.values(filters).some((v) => v !== "");

  return (
    <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 mb-5">
      <div className="flex items-center gap-2 mb-3">
        <SlidersHorizontal size={15} className="text-gray-500" />
        <span className="text-sm font-medium text-gray-700">Filters</span>
        {hasActiveFilters && (
          <button
            onClick={resetFilters}
            className="ml-auto flex items-center gap-1 text-xs text-gray-400 hover:text-gray-700 transition"
          >
            <X size={12} />
            Clear all
          </button>
        )}
      </div>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
        <input
          name="tagName"
          value={filters.tagName}
          onChange={handleChange}
          className="border border-gray-200 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-black"
          placeholder="Search by tag"
        />
        <input
          name="authorId"
          value={filters.authorId}
          onChange={handleChange}
          type="number"
          className="border border-gray-200 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-black"
          placeholder="Author ID"
        />
        <input
          name="from"
          value={filters.from}
          onChange={handleChange}
          type="date"
          className="border border-gray-200 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-black"
          title="From date"
        />
        <input
          name="to"
          value={filters.to}
          onChange={handleChange}
          type="date"
          className="border border-gray-200 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-black"
          title="To date"
        />
      </div>
    </div>
  );
};

export default FilterBar;
