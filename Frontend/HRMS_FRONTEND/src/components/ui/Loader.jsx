// components/ui/loader.tsx
import { Loader2 } from "lucide-react";
import React from "react";
import { cn } from "@/lib/utils"; // shadcn's className helper

export function Loader({ size = 24, className }) {
  return (
    <Loader2
      className={cn("animate-spin text-primary", className)}
      size={size}
    />
  );
}
