import React from 'react';

interface BrandMarkProps {
  size?: 'sm' | 'md' | 'lg';
  showWordmark?: boolean;
}

export const BrandMark: React.FC<BrandMarkProps> = ({ size = 'md', showWordmark = true }) => {
  const iconDimensions =
    size === 'sm' ? 'w-7 h-7' : size === 'lg' ? 'w-10 h-10' : 'w-8 h-8';

  const textSize =
    size === 'sm' ? 'text-base' : size === 'lg' ? 'text-xl' : 'text-lg';

  return (
    <div className="inline-flex items-center space-x-2.5 select-none">
      <div
        className={`${iconDimensions} rounded-lg bg-slate-900 flex items-center justify-center flex-shrink-0 border border-slate-800 shadow-xs`}
      >
        <svg
          viewBox="0 0 24 24"
          fill="none"
          className="w-4/6 h-4/6 text-white"
          xmlns="http://www.w3.org/2000/svg"
          aria-hidden="true"
        >
          <path
            d="M12 4L20 19H16.2L12 10.8L7.8 19H4L12 4Z"
            fill="currentColor"
          />
          <rect x="9.5" y="15.5" width="5" height="2" rx="0.5" fill="#3B82F6" />
        </svg>
      </div>
      {showWordmark && (
        <div className="flex items-baseline space-x-1">
          <span className={`${textSize} font-bold tracking-tight text-slate-900`}>
            Apti<span className="text-blue-600">Core</span>
          </span>
        </div>
      )}
    </div>
  );
};
