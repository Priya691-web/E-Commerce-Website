import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import ProductCard from '@components/ProductCard';

describe('ProductCard Component', () => {
  const mockProduct = {
    id: 1,
    name: 'Test Product',
    price: 99.99,
    image: '/test-image.jpg',
    category: 'Clothing',
    inStock: true,
  };

  it('renders product information correctly', () => {
    render(<ProductCard product={mockProduct} />);
    
    expect(screen.getByText('Test Product')).toBeInTheDocument();
    expect(screen.getByText('$99.99')).toBeInTheDocument();
  });

  it('handles add to cart click', () => {
    const mockOnAddToCart = vi.fn();
    render(<ProductCard product={mockProduct} onAddToCart={mockOnAddToCart} />);
    
    const addToCartButton = screen.getByText('Add to Cart');
    fireEvent.click(addToCartButton);
    
    expect(mockOnAddToCart).toHaveBeenCalledWith(mockProduct);
  });

  it('handles wishlist toggle', () => {
    const mockOnWishlistToggle = vi.fn();
    render(<ProductCard product={mockProduct} onWishlistToggle={mockOnWishlistToggle} />);
    
    const wishlistButton = screen.getByLabelText('Add to wishlist');
    fireEvent.click(wishlistButton);
    
    expect(mockOnWishlistToggle).toHaveBeenCalledWith(mockProduct.id);
  });

  it('displays out of stock when product is not in stock', () => {
    const outOfStockProduct = { ...mockProduct, inStock: false };
    render(<ProductCard product={outOfStockProduct} />);
    
    expect(screen.getByText('Out of Stock')).toBeInTheDocument();
  });

  it('displays sale badge when product is on sale', () => {
    const saleProduct = { ...mockProduct, onSale: true, salePrice: 79.99 };
    render(<ProductCard product={saleProduct} />);
    
    expect(screen.getByText('Sale')).toBeInTheDocument();
    expect(screen.getByText('$79.99')).toBeInTheDocument();
  });
});
