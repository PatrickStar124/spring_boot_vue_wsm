import { defineStore } from 'pinia'
import axios from 'axios'

export const useCartStore = defineStore('cart', {
    state: () => ({
        cartList: [],
        loading: false
    }),

    actions: {
        async addToCart(book, userId) {
            if (!book || !userId) return

            const existingItemIndex = this.cartList.findIndex(item => item.bookId === book.id)

            if (existingItemIndex > -1) {
                const existingItem = this.cartList[existingItemIndex]
                if (existingItem.quantity >= book.stock) {
                    alert('已达到最大库存，无法继续添加')
                    return
                }

                try {
                    const response = await axios.post('http://localhost:8090/cart/add', null, {
                        params: {
                            userId: userId,
                            bookId: book.id
                        }
                    })

                    if (response.data.code === 200) {
                        this.cartList[existingItemIndex].quantity += 1
                        alert('购物车数量已更新')
                    } else {
                        alert(response.data.msg || '更新购物车数量失败')
                    }
                } catch (error) {
                    console.error('更新购物车数量失败:', error)
                    alert('网络错误，请稍后重试')
                }
            } else {
                this.loading = true
                try {
                    const response = await axios.post('http://localhost:8090/cart/add', null, {
                        params: {
                            userId: userId,
                            bookId: book.id
                        }
                    })

                    if (response.data.code === 200) {
                        this.cartList.push({
                            id: response.data.data?.id || Date.now(),
                            bookId: book.id,
                            bookName: book.name || book.bookName || '未知图书',
                            price: book.price || 0,
                            quantity: 1,
                            stock: book.stock || 0,
                            image: this.fixImageUrl(book.imageUrl || ''),
                            author: book.author || '' // 添加作者信息
                        })
                        console.log('添加到购物车的书籍信息:', this.cartList[this.cartList.length - 1]) // 调试
                        alert('已加入购物车')
                    } else {
                        alert(response.data.msg || '加入购物车失败')
                    }
                } catch (error) {
                    console.error('加入购物车失败:', error)
                    alert('网络错误，请稍后重试')
                } finally {
                    this.loading = false
                }
            }
        },

        async initCartList(userId) {
            if (!userId) return
            this.loading = true
            try {
                const response = await axios.get('http://localhost:8090/cart/list', {
                    params: { userId: userId }
                })

                console.log('购物车接口返回的完整数据:', response.data) // 重要：查看数据结构

                if (response.data.code === 200) {
                    // 先查看数据结构
                    if (response.data.data && response.data.data.length > 0) {
                        console.log('第一个购物车项的结构:', response.data.data[0])
                        console.log('第一个购物车项的book对象:', response.data.data[0].book)
                        console.log('book对象的字段:', response.data.data[0].book ? Object.keys(response.data.data[0].book) : '无book对象')
                    }

                    this.cartList = response.data.data.map(item => {
                        console.log('处理单个购物车项:', item) // 调试每个项目

                        // 处理可能的多种数据结构
                        const book = item.book || {}
                        const bookName = book.name || book.bookName || item.bookName || '未知图书'
                        const price = book.price || item.price || 0
                        const stock = book.stock || item.stock || 0
                        const imageUrl = book.imageUrl || book.image || book.img || item.imageUrl || ''
                        const author = book.author || item.author || ''

                        return {
                            id: item.id,
                            bookId: item.bookId || book.id,
                            bookName: bookName,
                            price: price,
                            quantity: item.quantity || 1,
                            stock: stock,
                            image: this.fixImageUrl(imageUrl),
                            author: author //   添加作者信息123
                        }
                    })

                    console.log('处理后的购物车列表:', this.cartList) // 查看最终结果
                }
            } catch (error) {
                console.error('获取购物车列表失败:', error)
            } finally {
                this.loading = false
            }
        },

        fixImageUrl(url) {
            if (!url) return '';
            if (url.startsWith('http://') || url.startsWith('https://')) {
                return url;
            }
            return 'http://localhost:8090' + url;
        }
    },

    getters: {
        cartTotalQuantity: state => state.cartList.reduce((total, item) => total + item.quantity, 0),
        cartTotalPrice: state => state.cartList.reduce((total, item) => total + (item.price * item.quantity), 0).toFixed(2)
    }
})